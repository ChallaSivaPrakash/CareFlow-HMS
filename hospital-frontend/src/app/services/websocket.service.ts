import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
// @ts-ignore
import SockJS from 'sockjs-client/dist/sockjs';
import { Subject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private client: Client;
  private alertSubject = new Subject<any>();
  private chatSubject = new Subject<any>();
  private apiUrl = 'http://localhost:8080/api/chat';
  private connected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;

  constructor(private http: HttpClient) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      debug: (str) => {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    this.client.onConnect = (frame) => {
      console.log('Connected to WebSocket', frame);
      this.connected = true;
      this.reconnectAttempts = 0;
      this.subscribeToAlerts();
    };

    this.client.onDisconnect = () => {
      this.connected = false;
      console.log('STOMP: Disconnected');
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
      this.connected = false;
    };

    this.client.activate();
  }

  isConnected(): boolean {
    return this.connected;
  }

  private subscribeToAlerts() {
    this.client.subscribe('/topic/alerts', (message: Message) => {
      console.log('Received alert:', message.body);
      this.alertSubject.next(JSON.parse(message.body));
    });

    // Also subscribe to emergencies from the triage system
    this.client.subscribe('/topic/emergencies', (message: Message) => {
      console.log('Received emergency:', message.body);
      this.alertSubject.next(JSON.parse(message.body));
    });
  }

  subscribeToDepartmentChat(department: string) {
    if (this.client.connected) {
      this.client.subscribe('/topic/department.' + department, (message: Message) => {
        this.chatSubject.next(JSON.parse(message.body));
      });
    } else {
      // If not connected yet, try again when connected
      const originalOnConnect = this.client.onConnect;
      this.client.onConnect = (frame) => {
        if (originalOnConnect) originalOnConnect(frame);
        this.client.subscribe('/topic/department.' + department, (message: Message) => {
          this.chatSubject.next(JSON.parse(message.body));
        });
      };
    }
  }

  getAlerts(): Observable<any> {
    return this.alertSubject.asObservable();
  }

  getChatMessages(): Observable<any> {
    return this.chatSubject.asObservable();
  }

  getChatHistory(department: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/history/${department}`);
  }

  sendChatMessage(message: any) {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/chat.sendToDepartment',
        body: JSON.stringify(message)
      });
    }
  }

  triggerEmergencyOverride(payload: any) {
    if (this.client.connected) {
      this.client.publish({
        destination: '/app/emergency.trigger', 
        body: JSON.stringify(payload)
      });
    } else {
      console.error('Cannot trigger emergency: STOMP disconnected');
    }
  }
}
