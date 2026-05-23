import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebSocketService } from '../../services/websocket.service';
import { AuthService } from '../../services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.html',  // <-- Changed to match your file
  styleUrl: './chat.css'       // <-- Changed to match your file (and changed styleUrls to styleUrl)
})
export class ChatComponent implements OnInit, OnDestroy {
  isOpen = false;
  newMessage = '';
  messages: any[] = [];
  currentUser: string = 'Staff';
  userDepartment: string = 'General';
  private chatSub?: Subscription;

  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Extract name and department from JWT
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.currentUser = payload.name || payload.sub || 'Staff';
        this.userDepartment = payload.department || 'General';
      } catch (e) {
        console.error('Error decoding token', e);
      }
    }

    // Subscribe to incoming messages for the specific department
    if (this.webSocketService.getChatMessages) {
      this.chatSub = this.webSocketService.getChatMessages().subscribe(msg => {
        this.messages.push(msg);
      });
      this.webSocketService.subscribeToDepartmentChat(this.userDepartment);
    }

    // Load chat history for the specific department
    this.webSocketService.getChatHistory(this.userDepartment).subscribe(history => {
      this.messages = history;
    });
  }

  ngOnDestroy() {
    if (this.chatSub) this.chatSub.unsubscribe();
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;
    
    const msgPayload = {
      sender: this.currentUser,
      content: this.newMessage,
      department: this.userDepartment,
      timestamp: new Date().toISOString()
    };
    
    if (this.webSocketService.sendChatMessage) {
      this.webSocketService.sendChatMessage(msgPayload);
    } else {
      // Fallback if websocket service isn't updated yet
      this.messages.push(msgPayload); 
    }
    
    this.newMessage = '';
  }
}