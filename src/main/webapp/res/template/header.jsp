<header id="page-header"
        class="mdc-toolbar mdc-toolbar--fixed">
    <div class="mdc-toolbar__row">
        <section class="mdc-toolbar__section mdc-toolbar__section--align-start">
            <div id="tf-box-search"
                 class="mdc-text-field mdc-text-field--box mdc-text-field--upgraded mdc-ripple-upgraded"
                 data-demo-no-auto-js=""
                 style="--mdc-ripple-surface-width:214px; --mdc-ripple-surface-height:56px; --mdc-ripple-fg-size:128.4px; --mdc-ripple-fg-scale:1.80067; --mdc-ripple-fg-translate-start:22.8px, -28.6375px; --mdc-ripple-fg-translate-end:42.8px, -36.2px;">
                <input type="text" id="tf-box-search-field"
                       class="mdc-text-field__input mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">
                <label for="tf-box-search-field" class="mdc-text-field__label mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">SEARCH</label>
                <div class="mdc-text-field__bottom-line"></div>
            </div>
            <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                    onclick="leaveRoom()"
                    id="leave-button"
                    style="align-self: center;margin-left:16px;margin-right: 16px;">New Room
            </button>
        </section>
        <section class="mdc-toolbar__section mdc-toolbar__section--align-middle">
            <span class="mdc-toolbar__title"><a href="#" style="color:inherit;text-decoration:none;" onclick="followLink('/');">ProxSync</a></span>
        </section>
        <section class="mdc-toolbar__section mdc-toolbar__section--align-end">
            <section class="mdc-toolbar__section mdc-toolbar__section--align-start">
                <div id="register-row"
                     style="align-self:center;float:right;margin-right:16px;margin-left:auto;">
                    <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg mdc-button--align-middle"
                            onclick="register()"
                            id="register-button"
                            style="
                                    align-self: center;
                                    margin-left:10px;">Sign in
                    </button>
                </div>
                <div id="signout-row" style="align-self: center; margin-right: 16px; margin-left: auto;">
                    <div style="float:right;" onmouseover="clearTimeout(timeOut); menuTop.open = true;"
                         onmouseout="timeOut = setTimeout(function() {menuTop.open = false;},200);"
                         id="profile-mouseaction">
                        <%
                        String uid = LoginUtil.getUid(request);
                        %>
                        <a href="#">
                            <img src="https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/panda.svg?alt=media&token=6f4d5bf1-af69-4211-994d-66655456d91a"
                                 id="avatar-toolbar" class="user-avatar-toolbar" onclick="followLink('/profile/');">
                        </a>
                        <div class="mdc-simple-menu mdc-simple-menu--open-from-top-right" id="profile-menu"
                             tabindex="-1" style="top:64px;right:-14px;">
                            <ul class="mdc-simple-menu__items mdc-list" role="menu" id="profile-list"
                                aria-hidden="true">
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0"
                                    onclick="followLink('/profile/');">
                                    <span style="align-self:center;">Profile</span>
                                </li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0"
                                    onclick="followLink('/watchlist/');">
                                    <span style="align-self:center;">Watchlist</span>
                                </li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0"
                                    onclick="followLink('/airing/');">
                                    <span style="align-self:center;">Airing</span>
                                </li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0"
                                    onclick="followLink('/settings/');">
                                    <span style="align-self:center;">Settings</span>
                                </li>
                                <li class="mdc-list-divider" role="separator"></li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0"
                                    onclick="signout();">
                                    <span style="align-self:center;">Sign Out</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <i class="material-icons mdc-toolbar__icon"
                       style="float:right;font-size:40px;padding:8px!important;"
                       onclick="menuNotifications.open = !menuNotifications.open">
                        <%
                        List<Notification> notifications = Database.getNotifications(uid);
                            if (notifications.isEmpty()) {
                            %>
                            notifications_none
                            <%
                            } else {
                            %>
                            notifications_active
                            <%
                            }
                            %>
                    </i>
                    <div class="mdc-simple-menu mdc-simple-menu--open-from-top-right" tabindex="-1"
                         id="notification-menu" style="top:64px;right:72px;">
                        <ul class="mdc-simple-menu__items mdc-list" role="menu" aria-hidden="true">
                            <%if(!notifications.isEmpty()&& ! "".equals(uid)) {%>
                            <%
                            for (int i = 0; i < notifications.size(); i++) {
                            Notification n = notifications.get(i);
                            %>
                            <li class="mdc-list-item profile-list" role="menuitem" aria-disabled="true"
                                id="notifications-<%=n.getKey()%>">
                                <span style="align-self:center;z-index:5; ">
                                    <a href="javascript:void(0)"
                                       onclick="watchNext(event,'<%=n.getKey()%>');return false;"
                                       style="color:inherit;text-decoration: none"><%=n.getTitle()%>: <%=n.getLatestEpisode()%>/<%=n.getEpisodeCount()%></a>
                                    <i onclick="removeNotification('<%=n.getKey()%>');return false;"
                                       class="material-icons remove-notification">clear</i>
                                </span>
                            </li>
                            <% if (i < notifications.size() - 1) {%>
                            <li role="separator" class="mdc-list-divider" id="divider-<%=n.getKey()%>"></li>
                            <%}%>
                            <%}%>
                            <%}%>
                        </ul>
                    </div>
                    <span id="welcome-msg" class="mdc-toolbar__title"
                          style="margin-top:4px;float:right;align-self:center;"></span>
                </div>
            </section>
        </section>
    </div>
</header>