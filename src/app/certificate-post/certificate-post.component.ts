import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-certificate-post',
  templateUrl: './certificate-post.component.html',
  styleUrls: ['./certificate-post.component.css']
})
export class CertificatePostComponent {
  certificateForm: FormGroup;
  certificateList: any[];
  tableRows: any[];

  constructor(private http:HttpClient, private aroute:ActivatedRoute,  private messageService: MessageService ) {
    this.certificateForm = new FormGroup({
      certificateName: new FormControl('', Validators.required),
      userEmail: new FormControl('', [Validators.required, Validators.email]),
      fileType: new FormControl('PDF', Validators.required)
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
    const id = this.aroute.snapshot.paramMap.get('id');
    this.http.put(`http://localhost:7852/api/certificates/update/${id}`, data,{responseType:'text'})
      .subscribe((response) => {
        console.log(response);
        this.showSuccessMessage("Updated");
        this.http.get(`http://localhost:7852/api/certificates/renew/${id}`,{responseType:'text'})
      .subscribe((response) => {
        console.log(response);
        this.showSuccessMessage("Renewed")
      });
      });
      
  }

  ngOnInit(): void {
    const id = this.aroute.snapshot.paramMap.get('id');
    console.log(id);

    this.http.get<any[]>(`http://localhost:7852/api/certificates/all`)
    .subscribe((data) => {
      console.log(data)
      this.certificateList = data;
      console.log(this.certificateList);
       this.tableRows = data.filter((item)=> item.id ==id);
       this.certificateForm.patchValue({
        certificateName: this.tableRows[0]?.certificateName,
        userEmail: this.tableRows[0]?.userEmail,
        fileType: this.tableRows[0]?.fileType,
        createDate: this.tableRows[0]?.createDate
       });
    });
  }
        showSuccessMessage(data:string) {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: `Certificate ${data} successfully!` });
        }
}
