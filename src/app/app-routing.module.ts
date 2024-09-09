import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CertificateTableComponent } from './certificate-table/certificate-table.component';


const routes: Routes = [
  {path:'',component:CertificateTableComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
