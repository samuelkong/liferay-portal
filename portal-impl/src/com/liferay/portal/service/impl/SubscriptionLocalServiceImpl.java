/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Subscription;
import com.liferay.portal.model.SubscriptionConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.base.SubscriptionLocalServiceBaseImpl;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.social.model.SocialActivityConstants;

import java.util.Date;
import java.util.List;

/**
 * @author Charles May
 * @author Zsolt Berentey
 */
public class SubscriptionLocalServiceImpl
	extends SubscriptionLocalServiceBaseImpl {

	/**
	 * Subscribe the user to an entity with an instant frequency for
	 * notifications.
	 *
	 * <p>
	 * If there is no assetEntry with the className and classPK a new one will
	 * be created.
	 *
	 * <p>
	 * A social activity for the subscription is created using the assetEntry
	 * associated with the className and classPK or the recently created
	 * AssetEntry.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the group ID of entity
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @return the subscription
	 * @throws PortalException if the user or group is not found
	 * @throws SystemException if a system exception occurred
	 */
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK)
		throws PortalException, SystemException {

		return addSubscription(
			userId, groupId, className, classPK,
			SubscriptionConstants.FREQUENCY_INSTANT);
	}

	/**
	 * Subscribe the user to an entity with an instant frequency for
	 * notifications.
	 *
	 * <p>
	 * If there is no assetEntry with the className and classPK a new one will
	 * be created.
	 *
	 * <p>
	 * A social activity for the subscription is created using the assetEntry
	 * associated with the className and classPK or the recently created
	 * AssetEntry.
	 *
	 * @param  userId the primary key of the user
	 * @param  groupId the group ID of the entity
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @param  frequency the frequency for notifications
	 * @return the subscription
	 * @throws PortalException if the user or group is not found
	 * @throws SystemException if a system exception occurred
	 */
	public Subscription addSubscription(
			long userId, long groupId, String className, long classPK,
			String frequency)
		throws PortalException, SystemException {

		// Subscription

		User user = userPersistence.findByPrimaryKey(userId);
		long classNameId = PortalUtil.getClassNameId(className);
		Date now = new Date();

		long subscriptionId = counterLocalService.increment();

		Subscription subscription = subscriptionPersistence.fetchByC_U_C_C(
			user.getCompanyId(), userId, classNameId, classPK);

		if (subscription == null) {
			subscription = subscriptionPersistence.create(subscriptionId);

			subscription.setCompanyId(user.getCompanyId());
			subscription.setUserId(user.getUserId());
			subscription.setUserName(user.getFullName());
			subscription.setCreateDate(now);
			subscription.setModifiedDate(now);
			subscription.setClassNameId(classNameId);
			subscription.setClassPK(classPK);
			subscription.setFrequency(frequency);

			subscriptionPersistence.update(subscription);
		}

		if (groupId > 0) {

			// Asset

			try {
				assetEntryLocalService.getEntry(className, classPK);
			}
			catch (Exception e) {
				assetEntryLocalService.updateEntry(
					userId, groupId, subscription.getCreateDate(),
					subscription.getModifiedDate(), className, classPK, null, 0,
					null, null, false, null, null, null, null,
					String.valueOf(groupId), null, null, null, null, 0, 0, null,
					false);
			}

			// Social

			if (className.equals(MBThread.class.getName())) {
				MBThread mbThread = mbThreadLocalService.getMBThread(classPK);

				JSONObject extraDataJSONObject =
					JSONFactoryUtil.createJSONObject();

				extraDataJSONObject.put("threadId", classPK);

				socialActivityLocalService.addActivity(
					userId, groupId, MBMessage.class.getName(),
					mbThread.getRootMessageId(),
					SocialActivityConstants.TYPE_SUBSCRIBE,
					extraDataJSONObject.toString(), 0);
			}
			else {
				socialActivityLocalService.addActivity(
					userId, groupId, className, classPK,
					SocialActivityConstants.TYPE_SUBSCRIBE, StringPool.BLANK,
					0);
			}
		}

		return subscription;
	}

	/**
	 * Deletes the subscription with the primary key from the database. A social
	 * activity with the unsubscribe action is created.
	 *
	 * @param  subscriptionId the primary key of the subscription
	 * @return the subscription that was removed
	 * @throws PortalException if a subscription with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public Subscription deleteSubscription(long subscriptionId)
		throws PortalException, SystemException {

		Subscription subscription = subscriptionPersistence.fetchByPrimaryKey(
			subscriptionId);

		return deleteSubscription(subscription);
	}

	/**
	 * Deletes the subscription of a user to an entity from the database. A
	 * social activity with the unsubscribe action is created.
	 *
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @throws PortalException if the user or the subscription could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteSubscription(long userId, String className, long classPK)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);
		long classNameId = PortalUtil.getClassNameId(className);

		Subscription subscription = subscriptionPersistence.findByC_U_C_C(
			user.getCompanyId(), userId, classNameId, classPK);

		deleteSubscription(subscription);
	}

	/**
	 * Deletes the subscription from the database. A social activity with the
	 * unsubscribe action is created.
	 *
	 * @param  subscription the subscription
	 * @return the subscription that was removed
	 * @throws PortalException if the user or group associated to the
	 *         subscription are not found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public Subscription deleteSubscription(Subscription subscription)
		throws PortalException, SystemException {

		// Subscription

		subscriptionPersistence.remove(subscription);

		// Social

		AssetEntry assetEntry = assetEntryPersistence.fetchByC_C(
			subscription.getClassNameId(), subscription.getClassPK());

		if (assetEntry != null) {
			String className = PortalUtil.getClassName(
				subscription.getClassNameId());

			socialActivityLocalService.addActivity(
				subscription.getUserId(), assetEntry.getGroupId(), className,
				subscription.getClassPK(),
				SocialActivityConstants.TYPE_UNSUBSCRIBE, StringPool.BLANK, 0);
		}

		return subscription;
	}

	/**
	 * Deletes all the subscriptions of the user.
	 *
	 * @param  userId the primary key of the user
	 * @throws PortalException if the user or group associated to the
	 *         subscription are not found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteSubscriptions(long userId)
		throws PortalException, SystemException {

		List<Subscription> subscriptions = subscriptionPersistence.findByUserId(
			userId);

		for (Subscription subscription : subscriptions) {
			deleteSubscription(subscription);
		}
	}

	/**
	 * Deletes all the subscriptions to the entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @throws PortalException if the user or group associated to any
	 *         subscription are not found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteSubscriptions(
			long companyId, String className, long classPK)
		throws PortalException, SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		List<Subscription> subscriptions = subscriptionPersistence.findByC_C_C(
			companyId, classNameId, classPK);

		for (Subscription subscription : subscriptions) {
			deleteSubscription(subscription);
		}
	}

	/**
	 * Returns the subscription of the user to an entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @return the subscription
	 * @throws PortalException if the subscription could not be found
	 * @throws SystemException if a system exception occurred
	 */
	public Subscription getSubscription(
			long companyId, long userId, String className, long classPK)
		throws PortalException, SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		return subscriptionPersistence.findByC_U_C_C(
			companyId, userId, classNameId, classPK);
	}

	/**
	 * Returns all the subscription of the user to several entities.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPKs the primary key of the entities
	 * @return the subscriptions
	 * @throws SystemException if a system exception occurred
	 */
	public List<Subscription> getSubscriptions(
			long companyId, long userId, String className, long[] classPKs)
		throws SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		return subscriptionPersistence.findByC_U_C_C(
			companyId, userId, classNameId, classPKs);
	}

	/**
	 * Returns all the subscriptions to the entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @return the subscriptions
	 * @throws SystemException if a system exception occurred
	 */
	public List<Subscription> getSubscriptions(
			long companyId, String className, long classPK)
		throws SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		return subscriptionPersistence.findByC_C_C(
			companyId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the subscriptions of the user.
	 *
	 * @param  userId the primary key of the user
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of all the subscriptions of the user
	 * @throws SystemException if a system exception occurred
	 */
	public List<Subscription> getUserSubscriptions(
			long userId, int start, int end,
			OrderByComparator orderByComparator)
		throws SystemException {

		return subscriptionPersistence.findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the subscriptions of the user to an entity's class name.
	 *
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @return the subscriptions
	 * @throws SystemException if a system exception occurred
	 */
	public List<Subscription> getUserSubscriptions(
			long userId, String className)
		throws SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		return subscriptionPersistence.findByU_C(userId, classNameId);
	}

	/**
	 * Returns the number of subscriptions of the user.
	 *
	 * @param  userId the primary key of the user
	 * @return the number of subscriptions of the user
	 * @throws SystemException if a system exception occurred
	 */
	public int getUserSubscriptionsCount(long userId) throws SystemException {
		return subscriptionPersistence.countByUserId(userId);
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to the entity.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPK the primary key of the entity
	 * @return <code>true</code> if the user is subscribed to the entity;
	 *         <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	public boolean isSubscribed(
			long companyId, long userId, String className, long classPK)
		throws SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		Subscription subscription = subscriptionPersistence.fetchByC_U_C_C(
			companyId, userId, classNameId, classPK);

		if (subscription != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> if the user is subscribed to any of the
	 * entities.
	 *
	 * @param  companyId the primary key of the company
	 * @param  userId the primary key of the user
	 * @param  className the entity's class name
	 * @param  classPKs the primary key of the entities
	 * @return <code>true</code> if the user is subscribed to any of the
	 *         entities; <code>false</code> otherwise
	 * @throws SystemException if a system exception occurred
	 */
	public boolean isSubscribed(
			long companyId, long userId, String className, long[] classPKs)
		throws SystemException {

		long classNameId = PortalUtil.getClassNameId(className);

		int count = subscriptionPersistence.countByC_U_C_C(
			companyId, userId, classNameId, classPKs);

		if (count > 0) {
			return true;
		}
		else {
			return false;
		}
	}

}