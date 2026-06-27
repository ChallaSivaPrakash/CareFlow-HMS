import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingChatService } from '../../services/booking-chat.service';
import { AuthService } from '../../services/auth.service';
import { PatientService } from '../../services/patient.service';

interface Message {
  sender: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chat-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-widget.component.html',
  styleUrl: './chat-widget.component.css'
})
export class ChatWidgetComponent implements OnInit {
  isOpen = false;
  isLoading = false;
  messages: Message[] = [];
  sessionId = '';
  currentMessage = '';
  patientInfo: any = null;

  constructor(
    private bookingChatService: BookingChatService,
    private authService: AuthService,
    private patientService: PatientService
  ) { }

  ngOnInit(): void {
    // Generate a unique session ID
    this.sessionId = this.generateSessionId();
    
    // Check if user is a logged-in patient
    if (this.authService.isLoggedIn() && this.authService.getUserRole() === 'PATIENT') {
      this.patientService.getMyMedicalRecord().subscribe({
        next: (data) => {
          this.patientInfo = data;
          this.messages.push({
            sender: 'bot',
            text: `Welcome back, ${data.name}! How can I help you book your session today?`
          });
        },
        error: (err) => {
          console.error('Error fetching patient info', err);
          this.messages.push({
            sender: 'bot',
            text: "Hi there! I'm your CareFlow Virtual Receptionist. How can I help you book an appointment today?"
          });
        }
      });
    } else {
      this.messages.push({
        sender: 'bot',
        text: "Hi there! I'm your CareFlow Virtual Receptionist. How can I help you book an appointment today?"
      });
    }
  }

  private generateSessionId(): string {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substring(2, 10);
  }

  toggleChat(): void {
    this.isOpen = !this.isOpen;
  }

  sendMessage(): void {
    if (!this.currentMessage.trim() || this.isLoading) {
      return;
    }

    const userMessage = this.currentMessage.trim();
    
    // Add user message to chat
    this.messages.push({
      sender: 'user',
      text: userMessage
    });
    
    this.currentMessage = '';
    this.isLoading = true;

    // If we have patient info, we can attach it to the message
    let enhancedMessage = userMessage;
    if (this.patientInfo) {
      // Attach patient info to the message for the agent
      enhancedMessage = `[PATIENT: ${JSON.stringify({
        name: this.patientInfo.name,
        age: this.patientInfo.age,
        patientId: this.patientInfo.patientId
      })}] ${userMessage}`;
    }

    this.bookingChatService.sendMessage(this.sessionId, enhancedMessage).subscribe({
      next: (response) => {
        this.messages.push({
          sender: 'bot',
          text: response
        });
        this.isLoading = false;
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Error sending message:', err);
        this.messages.push({
          sender: 'bot',
          text: "Sorry, I'm having trouble connecting right now. Please try again later."
        });
        this.isLoading = false;
        this.scrollToBottom();
      }
    });
  }

  private scrollToBottom(): void {
    // Small delay to allow DOM update
    setTimeout(() => {
      const messageContainer = document.getElementById('chat-messages');
      if (messageContainer) {
        messageContainer.scrollTop = messageContainer.scrollHeight;
      }
    }, 100);
  }
}
