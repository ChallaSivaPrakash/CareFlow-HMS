import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface BookingChatRequest {
  session_id: string;
  message: string;
}

interface BookingChatResponse {
  response: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingChatService {
  private readonly apiUrl = 'http://localhost:8000/api/ai/chat/booking';

  constructor(private http: HttpClient) { }

  sendMessage(sessionId: string, message: string): Observable<string> {
    const payload: BookingChatRequest = {
      session_id: sessionId,
      message: message
    };

    return this.http.post<BookingChatResponse>(this.apiUrl, payload).pipe(
      map(res => res.response)
    );
  }
}
