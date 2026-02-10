import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  template: `
    <footer class="footer">
      <div class="main-footer">
        <div>
          <p>
            &copy; Todos os direitos reservados, 2025;<br>
            Site feito por alunos da UFAPE.
          </p>
        </div>
                
        <div>
          <a
            href="https://github.com/Engenharia-de-Software-2025-2/cinema-tickets"
            target="_blank"
            rel="noopener noreferrer"
          >
            <img src="github.png" class="github-icon">
          </a>
        </div>
      </div>
    </footer>
  `,
  styleUrl: './footer.css',
})
export class Footer {

}