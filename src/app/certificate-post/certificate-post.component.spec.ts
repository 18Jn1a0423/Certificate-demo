import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificatePostComponent } from './certificate-post.component';

describe('CertificatePostComponent', () => {
  let component: CertificatePostComponent;
  let fixture: ComponentFixture<CertificatePostComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificatePostComponent]
    });
    fixture = TestBed.createComponent(CertificatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
