import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  template: `
    <header class="header">
      <div class="main-header">
          <a href="/" class="logo-wrapper">
              <img src="ticket.png" class="logo-img">
              <h1 class="logo-title">Cinema-Tickets</h1>
          </a>
      </div>
    </header>
  `,
  styleUrl: './header.css',
})
export class Header {
  
}