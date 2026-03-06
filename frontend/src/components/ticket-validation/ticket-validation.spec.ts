import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketValidation } from './ticket-validation';

describe('TicketValidation', () => {
  let component: TicketValidation;
  let fixture: ComponentFixture<TicketValidation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketValidation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketValidation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
