import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CertificateTableComponent } from './certificate-table/certificate-table.component';
import { CertificatePostComponent } from './certificate-post/certificate-post.component';
import { CertificateUpdateComponent } from './certificate-update/certificate-update.component';

const routes: Routes = [
  {path:'',component:CertificateTableComponent},
  {path:'certificate-post',component:CertificatePostComponent},
  {path:'certificate-update/:id',component:CertificateUpdateComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
