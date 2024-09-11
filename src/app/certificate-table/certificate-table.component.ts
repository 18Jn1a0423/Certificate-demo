import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-certificate-table',
  templateUrl: './certificate-table.component.html',
  styleUrls: ['./certificate-table.component.css']
})
export class CertificateTableComponent {

  showPaginator: boolean = true; 
  // dataSource = new MatTableDataSource<any>([]);
  totalPages: number = 0;
  pageSize: number = 20;
  currentPage: number = 0;
  

  certificateList: any[] = [];
  addForm: boolean = false;
  tableRows: any[] = [];
  certificateForm: FormGroup;
  visibleEdit: boolean = false;
  filteredData: any[];

  constructor(private http: HttpClient, private route: Router, private messageService: MessageService) {

  }

  ngOnInit(): void {
    this.getAllCertificates();
    this.certificateForm = new FormGroup({
      certificateName: new FormControl('', Validators.required),
      userEmail: new FormControl('', [Validators.required, Validators.email]),
      fileType: new FormControl('', Validators.required),
      expiryDate: new FormControl('', Validators.required)
    });
  }

  getAllCertificates() {
    this.http.get<any[]>('http://172.17.12.38:8081/api/certificates/all')
      .subscribe((data) => {
        console.log(data)
        this.certificateList = data;
        console.log(this.certificateList);
        this.tableRows = data.map(item => ({
          id: item.id,
          certificateName: item.certificateName,
          createDate: item.createDate,
          expiryDate: item.expiryDate,
          fileData: item.fileData,
          fileType: item.fileType,
          userEmail: item.userEmail,
          createdBy: item.createdBy,
          reportManager: item.reportManager,
          reportDirector: item.reportDirector
        }));
      });
    }

  addCertificates() {
    const formData = this.certificateForm.value
    const certificateData = {
      certificateName: formData.certificateName,
      fileType: formData.fileType,
      userEmail: formData.userEmail,
      expiryDate: formData.expiryDate,
      createdBy: "Admin",
      reportManager: "pavanireddyjeeri123@gmail.com",
      reportDirector: "pavanireddyjeeri123@gmail.com"
    };

    this.http.post('http://172.17.12.38:8081/api/certificates/add', certificateData, {responseType: 'text'}).subscribe((response: any) => {
        console.log(response);
        this.showSuccessNessage("Created")
      }, (error) => {
        console.error('Error occurred while adding certificate:', error);
      });
  }
  showDialog() {
    this.addForm = true;
  }
  addCertificatButton() {
    this.route.navigate(['/insert'])
  }

  updateCertificate(id: string) {

    this.showDialog1();
    this.filteredData = this.certificateList.filter((item) => item.id == id);

    const expiryDate = new Date(this.filteredData[0].expiryDate);
    this.certificateForm.patchValue(({
      certificateName: this.filteredData[0].certificateName,
      userEmail: this.filteredData[0].userEmail,
      expiryDate: expiryDate
    }))

  }

  updateSubmitForm(id: any) {
    this.certificateForm.value;
    let data = {
      certificateName: this.certificateForm.value.certificateName,
      createDate: this.certificateForm.value.createDate,
      expiryDate: this.certificateForm.value.expiryDate,
      fileData: this.certificateForm.value.fileData,
      fileType: this.certificateForm.value.fileType,
      userEmail: this.certificateForm.value.userEmail,
      createdBy: this.certificateForm.value.createdBy,
    }
    
    {

      this.http.put(`http://172.17.12.38:8081/api/certificates/update/${id}`,   data,{responseType:'text' } )
        .subscribe((response) => {
          this.showSuccessNessage("Updated")
        });
    }
  }
  showDialog1() {
    this.visibleEdit = true;
  } 
  updateCertificateButton() {
    this.route.navigate(["/update-certificate"])
  } 
  showErrorMessage(data: string) {
    this.messageService.add({ severity: 'error', summary: 'Failed', detail: `Certificate ${data} failed!` });
  } 
  
  showSuccessNessage(data: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: `Certificate ${data} successfully!` });
  }


  deleteCertificate(userEmail: string) {
    this.http.delete(`http://172.17.12.38:8081/api/certificates/delete?userEmail=${userEmail}`,{responseType: 'text'})
      .subscribe((response) => {
        console.log(response);
        this.showSuccessMessage("Deleted");
      });
  }


  downloadCertificate(id: number, fileType: string) {
    const params = new HttpParams()
      .set('Id', id.toString())
      .set('fileType', fileType);  // fileType is now a string
    this.http.get(`http://172.17.12.38:8081/api/certificates/download`, { params, responseType: 'blob' })
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

  }

  renewCertificate(certificateId: number) {
    this.http.get(`http://localhost:8081/api/certificates/renew/${certificateId}`, { responseType: 'text' })
      .subscribe(
        (response) => {
          console.log(response);  // Log the response from the server
          this.showSuccessNessage("Renewed");  // Show success message
        },
        (error) => {
          console.error('Error renewing certificate:', error);  // Log the error if any
          this.showErrorMessage("Error renewing certificate.");  // Show error message
        }
      );
  }

  showSuccessMessage(data: string) {
    this.messageService.add({ severity: 'success', summary: 'Success', detail: `Certificate ${data} successfully!` });
  }


}


