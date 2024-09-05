import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutComponent } from './layout/layout.component';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { CertificateTableComponent } from './certificate-table/certificate-table.component';
import { HttpClientModule } from '@angular/common/http';
import { CertificateUpdateComponent } from './certificate-update/certificate-update.component';
import { CertificatePostComponent } from './certificate-post/certificate-post.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CalendarModule } from 'primeng/calendar';
@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    SidebarComponent,
    CertificateTableComponent,
    CertificateUpdateComponent,
    CertificatePostComponent
  ],
  imports: [
    BrowserModule,
    TableModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule, ReactiveFormsModule,BrowserAnimationsModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    CalendarModule
  ],
  providers: [MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
