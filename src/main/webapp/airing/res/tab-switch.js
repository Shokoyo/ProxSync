/**
 * Created by Jeremias on 17.09.2017.
 */
var dynamicTabBar = window.dynamicTabBar = new mdc.tabs.MDCTabBar(document.querySelector('#dynamic-tab-bar'));
var panels = document.getElementById("panels");
dynamicTabBar.preventDefaultOnClick = true;

function updatePanel(index) {
    var activePanel = panels.querySelector('.panel.active');
    if (activePanel) {
        activePanel.classList.remove('active');
    }
    var newActivePanel = panels.querySelector('.panel:nth-child(' + (index + 1) + ')');
    if (newActivePanel) {
        newActivePanel.classList.add('active');
    }
}

dynamicTabBar.listen('MDCTabBar:change', function (t) {
    var dynamicTabBar = t.detail;
    var nthChildIndex = dynamicTabBar.activeTabIndex;
    updatePanel(nthChildIndex);
    autoSizeText();
});
console.log(location.hash);
if(location.hash === "#short") {
    dynamicTabBar.activeTabIndex = 1;
    updatePanel(1);
} else if(location.hash === "#movie") {
    dynamicTabBar.activeTabIndex = 2;
    updatePanel(2);
} else if(location.hash === "#ova") {
    dynamicTabBar.activeTabIndex = 3;
    updatePanel(3);
}
