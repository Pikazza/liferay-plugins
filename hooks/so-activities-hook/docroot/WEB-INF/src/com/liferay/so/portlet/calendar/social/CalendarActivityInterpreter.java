/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.so.portlet.calendar.social;

import com.liferay.compat.portal.service.ServiceContext;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.calendar.model.CalEvent;
import com.liferay.portlet.calendar.service.CalEventLocalServiceUtil;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.so.activities.model.SOBaseSocialActivityInterpreter;

import java.text.Format;

/**
 * @author Evan Thibodeau
 */
public class CalendarActivityInterpreter
	extends SOBaseSocialActivityInterpreter {

	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected String getBody(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		StringBundler sb = new StringBundler(15);

		sb.append("<div class=\"activity-body\"><div class=\"title\">");

		String pageTitle = null;

		AssetRenderer assetRenderer = getAssetRenderer(activity);

		LiferayPortletRequest liferayPortletRequest =
			serviceContext.getLiferayPortletRequest();

		if (Validator.isNotNull(
				assetRenderer.getIconPath(liferayPortletRequest))) {

			pageTitle = wrapLink(
				getLinkURL(activity, serviceContext),
				assetRenderer.getIconPath(liferayPortletRequest),
				HtmlUtil.escape(
					assetRenderer.getTitle(serviceContext.getLocale())));
		}
		else {
			pageTitle = wrapLink(
				getLinkURL(activity, serviceContext),
				HtmlUtil.escape(
					assetRenderer.getTitle(serviceContext.getLocale())));
		}

		sb.append(pageTitle);
		sb.append("</div><div class=\"date\"><strong>");
		sb.append(serviceContext.translate("date"));
		sb.append(": </strong>");

		Format dateFormatDate = getFormatDateTime(
			serviceContext.getLocale(), serviceContext.getTimeZone());

		CalEvent event = CalEventLocalServiceUtil.getEvent(
			activity.getClassPK());

		sb.append(dateFormatDate.format((event.getStartDate())));
		sb.append("</div><div class=\"location\"><strong>");
		sb.append(serviceContext.translate("location"));
		sb.append(": </strong>");
		sb.append(event.getLocation());
		sb.append("</div><div class=\"description\"><strong>");
		sb.append(serviceContext.translate("description"));
		sb.append(": </strong>");
		sb.append(
			StringUtil.shorten(
				assetRenderer.getSummary(serviceContext.getLocale()), 200));
		sb.append("</div></div>");

		return sb.toString();
	}

	@Override
	protected String getLink(
			SocialActivity activity, ServiceContext serviceContext)
		throws Exception {

		return wrapLink(
			getLinkURL(activity, serviceContext),
			serviceContext.translate("view-calendar"));
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		if (activity.getType() == _ADD_EVENT) {
			return "added-a-new-calendar-event";
		}
		else if (activity.getType() == _UPDATE_EVENT) {
			return "updated-a-calendar-event";
		}

		return StringPool.BLANK;
	}

	private static final int _ADD_EVENT = 1;

	private static final String[] _CLASS_NAMES = {CalEvent.class.getName()};

	private static final int _UPDATE_EVENT = 2;

}