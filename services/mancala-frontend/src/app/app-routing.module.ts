import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MancalaComponent} from "./mancala/mancala.component";

const routes: Routes = [{
    path: "", component: MancalaComponent
}];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
