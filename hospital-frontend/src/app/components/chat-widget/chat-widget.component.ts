import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookingChatService } from '../../services/booking-chat.service';

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

  constructor(private bookingChatService: BookingChatService) { }

  ngOnInit(): void {
    // Generate a unique session ID
    this.sessionId = this.generateSessionId();
    
    // Add welcome message
    this.messages.push({
      sender: 'bot',
      text: "Hi there! I'm your CareFlow Virtual Receptionist. How can I help you book an appointment today?"
    });
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

    this.bookingChatService.sendMessage(this.sessionId, userMessage).subscribe({
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
