import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service'; // <-- ADDED
import { ChatComponent } from './components/chat/chat.component'; // <-- ADDED

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ChatComponent], // <-- ADDED ChatComponent
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('hospital-frontend');
  isRedAlertActive = false;
  alertMessage = 'EMERGENCY: RED ALERT TRIGGERED IN WARD';

  constructor(
    private webSocketService: WebSocketService,
    public authService: AuthService // <-- ADDED (Must be 'public' to expose it to the HTML template)
  ) {}

  ngOnInit() {
    this.webSocketService.getAlerts().subscribe((alert) => {
      this.triggerRedAlert(alert);
    });
  }

  triggerRedAlert(alert: any) {
    // Activate banner
    this.isRedAlertActive = alert.active;
    
    // Set custom message
    this.alertMessage = "EMERGENCY: INCOMING TRAUMA CASES. PLEASE BE ALERT.";
  }
}