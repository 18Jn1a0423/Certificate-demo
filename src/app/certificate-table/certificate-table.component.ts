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
  certificateList: any[] = [];
  pdfUrl: string;
  visible: boolean = false;
  certificateNo: string;
  tableRows = [
    {
      id: 1,
      certificateName: 'Certificate 1',
      userEmail: 'user1@example.com',
      createdBy: 'Admin',
      fileType: 'PDF',
      reportManager: '2024-01-01',
      expiryDate: '2024-12-31'
    },
    {
      id: 2,
      certificateName: 'Certificate 2',
      userEmail: 'user2@example.com',
      createdBy: 'Admin',
      fileType: 'CSV',
      reportManager: '2024-02-15',
      expiryDate: '2024-11-15'
    },
    {
      id: 3,
      certificateName: 'Certificate 3',
      userEmail: 'user3@example.com',
      createdBy: 'User',
      fileType: 'DOCX',
      reportManager: '2024-03-20',
      expiryDate: '2024-09-30'
    }
  ];
  certificateForm: FormGroup;
  visibleEdit: boolean = false;
  constructor(private http: HttpClient, private route:Router,private messageService: MessageService ) {
    this.certificateForm = new FormGroup({
      certificateName: new FormControl('', Validators.required),
      userEmail: new FormControl('', [Validators.required, Validators.email]),
      fileType: new FormControl('PDF', Validators.required)
    });
  }

  ngOnInit(): void {
  
    this.loadCertificates();
  }


  loadCertificates() {
    // const fileTypes: string[] = ['pdf', 'csv', 'docx', 'xlsx'];
    // this.http.get<any[]>(`http://localhost:7852/api/certificates/type?fileType=csv`)
    //   .subscribe(data => {
    //     this.certificateList = data;
    //     console.log(this.certificateList);
    //     this.tableRows = data.map(item => ({
    //       certificateName: item.certificateName,
    //       createDate: item.createDate,
    //       expiryDate:  item.expiryDate,
    //       fileData: item.fileData,
    //       fileType: item.fileType,
    //       userEmail: item.userEmail,
    //       createdBy: item.createdBy,
    //       reportManager: item.reportManager,
    //       reportDirector: item.reportDirector
    //     }));
    //     console.log("this.tableRows",this.tableRows)
    //   });
  
    this.http.get<any[]>(`http://localhost:7852/api/certificates/all`)
      .subscribe((data) => {
        console.log(data)
        this.certificateList = data;
        console.log(this.certificateList);
        this.tableRows = data.map(item => ({
          id:item.id,
          certificateName: item.certificateName,
          createDate: item.createDate,
          expiryDate:  item.expiryDate,
          fileData: item.fileData,
          fileType: item.fileType,
          userEmail: item.userEmail,
          createdBy: item.createdBy,
          reportManager: item.reportManager,
          reportDirector: item.reportDirector
        }));
      });
  }
  // this.http.get<any[]>(`http://localhost:7852/api/certificates/type?fileType=pdf`)
  editCertificate(certificateId: number) {
    // this.route.navigate(["/certificate-update",certificateId]);

    this.visibleEdit = true;
  }

  // removeCertificate(email: string) {
  
  //   console.log(email)
  //   this.http.delete(`http://localhost:7852/api/certificates/delete?userEmail=${email}`)
  //     .subscribe((response) => {
  //      console.log(response);
  //      this.ngOnInit()
  //     });
  // }
  addCertificate() {
    this.http.post(`http://localhost:7852/api/certificates/add`,{})
      .subscribe((response) => {
        console.log(response);
      });
  }
  showDialog() {
    this.visible = true;
}
  addcertificateButton(){

    this.route.navigate(["/certificate-post"])
  }

  downloadCertificate(id: number, fileType: string) {
    console.log(id);
    const params = new HttpParams()
      .set('Id', id.toString()) // Convert Long to string
      .set('fileType', fileType);
      console.log(id);
    this.http.get(`http://localhost:7852/api/certificates/download`, { params, responseType: 'blob' })
      .subscribe((response: any) => {
        const blob = new Blob([response], { type: `application/${fileType}` });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `certificate.${fileType}`; // set the file name
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  deleteCertificate(item:any)
  {
    this.showError()
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
  showError() {
    this.messageService.add({ severity: 'success', summary: '', detail: 'Deleted Succesfully' });
}
}
