console.log('sync - before');
var done = arguments[0];
window
    .getAngularTestability(document.querySelector('app'))
    .whenStable(function(didWork) {
        console.log('sync - stable ' + didWork);
        done(didWork);
    });
console.log('sync - after');
