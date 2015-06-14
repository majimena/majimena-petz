'use strict';

angular.module('majimenatestApp')
    .controller('SidebarController', function ($rootScope, $scope) {

        $scope.isToggleSideMenu = function() {
            return $rootScope.getSetting('minifySideMenu');
        };

        $scope.loadSideMenu = function() {
        	var toggle = $rootScope.getSetting('minifySideMenu');
        	$scope.toggleSideMenu(toggle);
        	$scope.minimum = toggle;
        };

        $scope.selectedMenu = 'dashboard';
        $scope.collapseVar = 0;

        $scope.toggleSideMenu = function(toggle) {
            $rootScope.addSetting('minifySideMenu', toggle);
            $scope.minimum = toggle;
        };

        $scope.check = function(x) {
            if (x === $scope.collapseVar) {
                $scope.collapseVar = 0;
            } else {
                $scope.collapseVar = x;
            }
        };
    })
;
