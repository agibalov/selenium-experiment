console.log('sync - before');
var done = arguments[0];
window
    .getAngularTestability(document.querySelector('app'))
    .whenStable(function() {
        console.log('sync - stable');
        done();
    });
console.log('sync - after');
