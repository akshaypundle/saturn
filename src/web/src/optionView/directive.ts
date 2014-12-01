module Saturn.OptionView {

    export class Directive implements ng.IDirective {
        public restrict = "E";
        public replace = true;
        public templateUrl = "src/optionView/template.html";
        public scope = {
            columns: "@",
            data: "@",
        };
    }

    directives.directive("saturn.optionView.directive", () => new Saturn.OptionView.Directive());
}