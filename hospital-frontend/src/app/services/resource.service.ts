import { Injectable, signal } from '@angular/core'; 
 
 @Injectable({ providedIn: 'root' }) 
 export class ResourceService<T> { 
   private dataSignal = signal<T | null>(null); 
   private loadingSignal = signal(false); 
   private errorSignal = signal<string | null>(null); 
 
   readonly data = this.dataSignal.asReadonly(); 
   readonly loading = this.loadingSignal.asReadonly(); 
   readonly error = this.errorSignal.asReadonly(); 
 
   setData(data: T): void { 
     this.dataSignal.set(data); 
     this.loadingSignal.set(false); 
     this.errorSignal.set(null); 
   } 
 
   setLoading(loading: boolean): void { 
     this.loadingSignal.set(loading); 
   } 
 
   setError(error: string): void { 
     this.errorSignal.set(error); 
     this.loadingSignal.set(false); 
   } 
 } 
