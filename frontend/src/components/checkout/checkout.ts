import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { CheckoutService } from '../../general-service/checkout-service/checkout-service';
import { CompraResumo } from '../../app/core/models/checkout.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css'
})
export class Checkout implements OnInit {
  private readonly router = inject(Router);
  private readonly service = inject(CheckoutService);
  
  informaçõesModal: any;
  compra?: CompraResumo;
  metodoPagamento: 'pix' | 'cartao_credito' | 'cartao_debito' | 'dinheiro' = 'pix';
  isProcessing = false;

  ngOnInit() {
  const data = localStorage.getItem('checkout_data');

  if (data) {
    this.compra = JSON.parse(data);
    this.informaçõesModal = this.compra; // Mantendo sua variável de controle
    console.log('Dados recuperados do LocalStorage:', this.compra);
    
    // Opcional: Limpar logo após ler para não sobrar lixo no navegador
    // localStorage.removeItem('checkout_data'); 
  } else {
    console.warn('Nenhum dado de compra encontrado no armazenamento.');
    this.router.navigate(['/']); 
  }
}

  async processarPagamento() {
    this.isProcessing = true;

    const deParaMetodo: any = {
        'pix': 'PIX',
        'cartao_credito': 'CARTAO_CREDITO',
        'cartao_debito': 'CARTAO_DEBITO',
        'dinheiro': 'DINHEIRO'
    };

    const payload = {
        sessao_id: this.informaçõesModal.sessaoId,
        assentos_ids: this.informaçõesModal.assentosIds,
        valor_esperado: this.informaçõesModal.valorTotal,
        metodo: deParaMetodo[this.metodoPagamento],
        token_pagamento: "token_pagamento" 
    };

    console.log('Payload para backend ', payload);

    try {
        const resultado = await this.service.processarPagamento(payload);
        
        if (resultado.status === 'aprovado') {
        Swal.fire({
            icon: 'success',
            title: 'Pedido Confirmado!',
            text: resultado.mensagem,
            confirmButtonColor: '#28a745'
        }).then(() => {
            // Redireciona para tela de sucesso com o ID do ingresso
            this.router.navigate(['/pedido-confirmado'], { queryParams: { id: resultado.ingresso_id } });
        });
        }
    } catch (error: any) {
        // Exibe modal explicativo em caso de erro (Saldo insuficiente, assentos ocupados, etc)
        Swal.fire({
        icon: 'error',
        title: 'Pagamento Negado',
        text: error.message,
        confirmButtonColor: '#c91432'
        });
    } finally {
        this.isProcessing = false;
    }
    }
}