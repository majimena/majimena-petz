'use strict';

angular.module('petzioApp')
    .controller('ClinicDetailController', function ($scope, $stateParams, Clinic) {
        $scope.clinic = {};
        $scope.load = function (id) {
            Clinic.get({id: id}, function(result) {
              $scope.clinic = result;
            });
        };
        $scope.load($stateParams.id);
    });
