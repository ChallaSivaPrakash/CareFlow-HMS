import { Component, signal, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router'; // <-- INJECT ROUTER
import { CommonModule } from '@angular/common';
import { WebSocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';
import { ChatWidgetComponent } from './components/chat-widget/chat-widget.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ChatWidgetComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('hospital-frontend');
  isRedAlertActive = false;
  isEmergencyActive = false;
  alertMessage = 'EMERGENCY: RED ALERT TRIGGERED IN WARD';

  constructor(
    private webSocketService: WebSocketService,
    public authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() {
    this.webSocketService.getAlerts().subscribe((alert) => {
      this.triggerRedAlert(alert);
    });
  }

  // --- NEW: Computes if FAB should be visible ---
  get isFabVisible(): boolean {
    return this.authService.getToken() !== null && this.router.url !== '/login';
  }

  triggerRedAlert(alert: any) {
    this.isRedAlertActive = alert.active;
    this.isEmergencyActive = alert.active;
    
    // --- NEW: Display who triggered it ---
    if(alert.active) {
      const sender = alert.triggeredBy ? alert.triggeredBy.toUpperCase() : 'A STAFF MEMBER';
      this.alertMessage = `🚨 EMERGENCY: TRAUMA OVERRIDE INITIATED BY ${sender} 🚨`;
    }
    this.cdr.detectChanges();
  }

  toggleEmergency() {
    this.isEmergencyActive = !this.isEmergencyActive;
    
    // --- NEW: Extract username from JWT ---
    let triggerUser = 'Unknown Staff';
    try {
      const token = this.authService.getToken();
      if(token) triggerUser = JSON.parse(atob(token.split('.')[1])).sub;
    } catch(e) {}

    // Send payload with the user's name
    this.webSocketService.triggerEmergencyOverride({ 
      active: this.isEmergencyActive,
      triggeredBy: triggerUser
    });
  }
}