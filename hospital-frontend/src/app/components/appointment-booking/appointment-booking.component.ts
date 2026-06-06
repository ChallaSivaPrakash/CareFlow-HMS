import { Component, signal, inject, OnInit } from '@angular/core'; 
 import { CommonModule } from '@angular/common'; 
 import { FormsModule } from '@angular/forms'; 
 import { AppointmentService } from '../../services/appointment.service'; 
 import { AuthService } from '../../services/auth.service'; 
 
 @Component({ 
   selector: 'app-appointment-booking', 
   standalone: true, 
   imports: [CommonModule, FormsModule], 
   template: ` 
     <div class="container mt-4"> 
       <h2>Book Appointment</h2> 
       
       <div class="mb-3"> 
         <label>Select Doctor</label> 
         <select [(ngModel)]="selectedDoctorId" class="form-control" (change)="loadSlots()"> 
           <option *ngFor="let doctor of doctors()" [value]="doctor.id"> 
             Dr. {{ doctor.name }} - {{ doctor.department }} 
           </option> 
         </select> 
       </div> 
 
       <div class="mb-3"> 
         <label>Select Date</label> 
         <input type="date" [(ngModel)]="selectedDate" class="form-control" (change)="loadSlots()"> 
       </div> 
 
       @if (slots().length > 0) { 
         <div class="slot-grid"> 
           @for (slot of slots(); track slot) { 
             <button class="btn btn-outline-primary m-1" (click)="bookSlot(slot)"> 
               {{ slot | date:'shortTime' }} 
             </button> 
           } 
         </div> 
       } @else if (loading()) { 
         <div class="spinner-border" role="status"> 
           <span class="visually-hidden">Loading...</span> 
         </div> 
       } 
     </div> 
   `, 
   styles: [` 
     .slot-grid { display: flex; flex-wrap: wrap; gap: 8px; } 
   `] 
 }) 
 export class AppointmentBookingComponent implements OnInit { 
   private appointmentService = inject(AppointmentService); 
   private authService = inject(AuthService); 
   
   doctors = signal<any[]>([]); 
   slots = signal<string[]>([]); 
   loading = signal(false); 
   selectedDoctorId: number = 0; 
   selectedDate: string = ''; 
 
   ngOnInit() { 
     this.loadDoctors(); 
   } 
 
   loadDoctors() { 
     this.appointmentService.getDoctors().subscribe({ 
       next: (data) => this.doctors.set(data) 
     }); 
   } 
 
   loadSlots() { 
     if (this.selectedDoctorId && this.selectedDate) { 
       this.loading.set(true); 
       this.appointmentService.getAvailableSlots(this.selectedDoctorId, this.selectedDate) 
         .subscribe({ 
           next: (data) => { 
             this.slots.set(data); 
             this.loading.set(false); 
           }, 
           error: () => this.loading.set(false) 
         }); 
     } 
   } 
 
   bookSlot(slot: string) { 
     this.appointmentService.bookAppointment({ 
       patientId: 1, // Change to actual logic: this.authService.getUserId() if available 
       doctorId: this.selectedDoctorId, 
       startTime: new Date(this.selectedDate + 'T' + slot), 
       endTime: new Date(this.selectedDate + 'T' + 
         new Date(new Date('1970-01-01T' + slot).getTime() + 30*60000).toTimeString().substring(0, 5)) 
     }).subscribe({ 
       next: () => { 
           alert('Appointment booked successfully!'); 
           this.loadSlots(); // refresh slots 
       }, 
       error: (err) => alert(err.error || 'Booking failed') 
     }); 
   } 
 } 
