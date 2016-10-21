import {NgModule, Injectable, OnInit, OnDestroy} from '@angular/core';
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
        console.log('ApiClient::getMessage() before');
        const response: Response = await this.http.get('http://localhost:8080/api/message').toPromise();
        const body: MessageDto = response.json();
        console.log('ApiClient::getMessage() after');
        return body.message;
    }
}

@Component({
    selector: 'app',
    template: `<div>
    <h1>message is {{message}}</h1>
    <button type="button" (click)="loadMessage()">Load Message</button>    
  </div>`
})
class AppComponent implements OnInit, OnDestroy {
    public message: string = '';

    constructor(private apiClient: ApiClient) {
    }

    async loadMessage(): Promise<void> {
        console.log('AppComponent::loadMessage(): before');
        this.message = '';
        const message: string = await this.apiClient.getMessage();

        await new Promise((resolve) => setTimeout(resolve, 1000));
        await this.apiClient.getMessage();
        await new Promise((resolve) => setTimeout(resolve, 1000));
        await this.apiClient.getMessage();
        await new Promise((resolve) => setTimeout(resolve, 1000));
        await this.apiClient.getMessage();
        await new Promise((resolve) => setTimeout(resolve, 1000));
        await this.apiClient.getMessage();

        this.message = message;
        console.log('AppComponent::loadMessage(): after');
    }

    addNumbers(a: number, b: number): number {
        console.log(`someone wants to add ${a} and ${b} (and BTW this.message is ${this.message})`);
        return a + b;
    }

    ngOnInit(): void {
        (<any>window).addNumbers = this.addNumbers.bind(this);
        console.log('exposed an addNumbers');
    }

    ngOnDestroy(): void {
        delete (<any>window).addNumbers;
        console.log('destroyed an addNumbers');
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
