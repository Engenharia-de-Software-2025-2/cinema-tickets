import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthMock {
  private tokenKey = 'auth_token';

  async login(email: string, password: string): Promise<{ success: boolean; message?: string }> {
    await this.delay(1000);
    
    const validCredentials = [
      { email: 'cinematickets123@gmail.com', password: 'Senha_Super_Secreta' },
      { email: 'teste@teste.com', password: '123456' },
      { email: 'admin@admin.com', password: 'admin123' }
    ];
    
    const isValid = validCredentials.some(
      cred => cred.email === email && cred.password === password
    );
    
    if (isValid) {
      const mockToken = this.generateMockToken(email);
      localStorage.setItem(this.tokenKey, mockToken);
      
      return { success: true };
    } else {
      return { 
        success: false, 
        message: this.getRandomErrorMessage() 
      };
    }
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }
  
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
  
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
  
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
  
  private generateMockToken(email: string): string {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({ 
      email: email,
      userId: Math.floor(Math.random() * 1000),
      exp: Date.now() + 86400000
    }));
    const signature = btoa('mock-signature-' + Date.now());
    
    return `${header}.${payload}.${signature}`;
  }
  
  private getRandomErrorMessage(): string {
    const errors = [
      'Email ou senha inválidos',
      'Credenciais incorretas',
      'Usuário não encontrado',
      'Conta desativada',
      'Muitas tentativas. Tente novamente em 5 minutos'
    ];
    return errors[Math.floor(Math.random() * errors.length)];
  }

}
