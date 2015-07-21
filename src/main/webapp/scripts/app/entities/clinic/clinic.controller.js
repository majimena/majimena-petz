'use strict';

angular.module('petzioApp')
    .controller('ClinicController', function ($scope, Notify, Clinic, ParseLinks) {
        $scope.clinics = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Clinic.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.clinics = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Clinic.get({id: id}, function(result) {
                $scope.clinic = result;
                $('#deleteclinicConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Clinic.delete({id: id},
                function () {
                    Notify.success('プロジェクトを削除しました。');
                    $scope.loadAll();
                    $('#deleteclinicConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.clinic = {name: null, description: null, owner: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };
    });
