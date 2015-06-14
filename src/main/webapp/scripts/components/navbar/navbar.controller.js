'use strict';

angular.module('majimenatestApp')
    .controller('NavbarController', function ($rootScope, $scope, $location, $state, Auth, Principal) {
        $scope.rootScope = $rootScope;

        if (!$rootScope.account) {
            Principal.identity(false).then(function(account) {
                $rootScope.account = account;
            });
        }

        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;

        $scope.showSideMenu = !!$state.current.data.requireSideMenu;

        $scope.logout = function () {
            Auth.logout();
            $state.go('home');
        };
    });
