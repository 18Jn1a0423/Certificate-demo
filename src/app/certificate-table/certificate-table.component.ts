import { HttpClient, HttpParams } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-certificate-table',
  templateUrl: './certificate-table.component.html',
  styleUrls: ['./certificate-table.component.css']
})
export class CertificateTableComponent {

  certificateList: any[]=[];
  visible: boolean =false;
  tableRows: any[]=[];
  certificateForm: FormGroup;
  visibleEdit: boolean=false;

  constructor(private http: HttpClient, private route: Router, private messageService: MessageService){
    this.certificateForm = new FormGroup({
      certificateName: new FormControl('', Validators.required),
      userEmail: new FormControl('',[Validators.required,Validators.email]),
      fileType: new FormControl('PDF', Validators.required)
    });
  }

  ngOnInit(): void{
    this.getAllCertificates();
  }

  getAllCertificates(){
    this.http.get<any[]>('http://localhost:8081/api/certificates/all')
    .subscribe((data)=>{
      console.log(data)
      this.certificateList = data;
      console.log(this.certificateList);
      this.tableRows = data.map(item => ({
        id:item.id,
        certificateName:item.certificateName,
        createDate:item.createDate,
        expiryDate:item.expiryDate,
        fileData:item.fileData,
        fileType:item.fileType,
        userEmail:item.userEmail,
        createdBy:item.createdBy,
        reportManager:item.reportManager,
        reportDirector:item.reportDirector
      }));
    });
  }

  addCertificates(){
  this.http.post('http://localhost:8081/api/certificates/add',{})
  .subscribe((response)=>{
    console.log(response);
    this.ngOnInit()
  });
  }showDialog(){
    this.visible=true;
  }addCertificateButton(){
    this.route.navigate(["/insert-certificate"])
  }

  updateCertificate(id:number,data:{
    certificateName: String,
    createDate: String,
    expiryDate: String,
    fileData: String,
    fileType: String,
    userEmail: String,
    createdBy: String,
    reportManager: String,
    reportDirector: String
  }){this.http.put(`http://localhost:8081/api/certificates/update?id=${id}`, data,{responseType:'text'})
  .subscribe((response)=>{
    this.ngOnInit()
  });
}showDialog1(){
  this.visible=true;
}updateCertificateButton(){
  this.route.navigate(["/update-certificate"])
} showErrorMessage(data: string) {
    this.messageService.add({ severity: 'error', summary: 'Failed', detail: `Certificate ${data} failed!`});
  }showSuccessNessage(data:string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: `Certificate ${data} successfully!` });
  }

  deleteCertificate(email: string) {
    this.http.delete(`http://localhost:8081/api/certificates/delete?userEmail=${email}`)
      .subscribe((response) => {
       console.log(response);
       this.ngOnInit()
      });
  }

  downloadCertificate(id: number, fileType: string) {  // change String to string
    const params = new HttpParams()
      .set('Id', id.toString())
      .set('fileType', fileType);  // fileType is now a string
    this.http.get(`http://localhost:8081/api/certificates/download`, { params, responseType: 'blob' })
      .subscribe((response: any) => {
        const blob = new Blob([response], { type: `application/${fileType}` });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `certificate.${fileType}`;  // Ensure the file is downloaded with the correct extension
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }
  onSubmit() {
    const today = new Date();
    console.log(this.certificateForm.value);
    const oneWeekFromToday = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    const expiryDate = oneWeekFromToday.toISOString().split('T')[0];
    let data = {
      certificateName: this.certificateForm.value.certificateName,
      userEmail: this.certificateForm.value.userEmail,
      fileType: this.certificateForm.value.fileType,
      createDate: this.certificateForm.value.createDate,
      expiryDate: expiryDate,
      createdBy: "admin",
      reportManager: "affijaroshan@gmail.com",
      reportDirector: "pavanireddyjeeri123@gmail.com"
    };
    // const id = this.aroute.snapshot.paramMap.get('id');
    this.http.put(`http://localhost:7852/api/certificates/update/1`, data,{responseType:'text'})
      .subscribe((response) => {
        console.log(response);
        this.showSuccessMessage("Updated");
        this.http.get(`http://localhost:7852/api/certificates/renew/1`,{responseType:'text'})
      .subscribe((response) => {
        console.log(response);
        this.showSuccessMessage("Renewed")
      });
      });
      
  }
  showSuccessMessage(data:string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: `Certificate ${data} successfully!` });
  }
}

