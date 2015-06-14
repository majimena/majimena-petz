'use strict';

angular.module('majimenatestApp')
    .factory('Notify', ['ngNotify', function (ngNotify) {
        var service = {};

        service.success = function(message) {
            ngNotify.set('<i class="fa fa-check fa-fw"></i>' + message, {
                position: 'top',
                type: 'success',
                duration: 3000,
                sticky: false,
                html: true
            });
        };

        service.error = function(message) {
            ngNotify.set('<i class="fa fa-times-circle fa-fw"></i>' + message, {
                position: 'top',
                type: 'error',
                sticky: true,
                html: true
            });
        };

        return service;
    }]);
