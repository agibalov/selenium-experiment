import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Component } from "@angular/core";

@Component({
    selector: 'app',
    template: `<div>
    <h1>hello world {{counter}}</h1>
    <button type="button" (click)="increment()" class="increase">Increment</button>
  </div>`
})
class AppComponent {
    public counter: number = 0;

    async increment(): Promise<void> {
        ++this.counter;
    }
}

@NgModule({
    imports: [ BrowserModule ],
    declarations: [
        AppComponent
    ],
    providers: [],
    bootstrap: [ AppComponent ]
})
export class AppModule {
}
