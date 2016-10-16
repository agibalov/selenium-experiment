console.log('before');
var done = arguments[0];
angular
    .getTestability(document.body)
    .whenStable(function() {
        console.log('stable');
        done();
    });
console.log('after');
