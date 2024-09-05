import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateUpdateComponent } from './certificate-update.component';

describe('CertificateUpdateComponent', () => {
  let component: CertificateUpdateComponent;
  let fixture: ComponentFixture<CertificateUpdateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificateUpdateComponent]
    });
    fixture = TestBed.createComponent(CertificateUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
