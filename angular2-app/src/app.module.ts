import {NgModule, Injectable} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Component } from "@angular/core";
import { HttpModule, Http, Response } from "@angular/http";

interface MessageDto {
    message: string;
}

@Injectable()
class ApiClient {
    constructor(private http: Http) {
    }

    async getMessage(): Promise<string> {
        const response: Response = await this.http.get('http://localhost:8080/api/message').toPromise();
        const body: MessageDto = response.json();
        return body.message;
    }
}

@Component({
    selector: 'app',
    template: `<div>
    <h1>message is {{message}}</h1>
    <button type="button" (click)="loadMessage()" class="load-message">Load Message</button>    
  </div>`
})
class AppComponent {
    public message: string = null;

    constructor(private apiClient: ApiClient) {
    }

    async loadMessage(): Promise<void> {
        this.message = '';
        const message: string = await this.apiClient.getMessage();
        this.message = message;
    }
}

@NgModule({
    imports: [ BrowserModule, HttpModule ],
    declarations: [
        AppComponent
    ],
    providers: [ ApiClient ],
    bootstrap: [ AppComponent ]
})
export class AppModule {
}
