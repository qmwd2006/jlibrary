/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.client.ui.security;

import java.util.HashMap;
import java.util.Iterator;

import org.jlibrary.client.ui.repository.RepositoryRegistry;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Member;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Registry of user and groups. This will be the unique access to user and groups
 * within client code. 
 */
public class MembersRegistry {
	
	static Logger logger = LoggerFactory.getLogger(MembersRegistry.class);
	
	private static MembersRegistry instance;

	private HashMap members = new HashMap();
	
	private MembersRegistry() {
		
		super();
	}
	
	/**
	 * Adds a member to the registry
	 * 
	 * @param member Member to be added
	 */
	public void addMember(Member member) {

		members.put(member.getId(),member);
		
		if (member instanceof Group) {
			Group group = (Group)member;
			if (group.getUsers() != null) {
				Iterator it = group.getUsers().iterator();
				while (it.hasNext()) {
					User user = (User) it.next();
					addMember(user);
				}
			}
		}
	}
	
	/**
	 * Removes a member from the registry
	 * 
	 * @param member Member to be removed
	 */
	public void removeMember(Member member) {

		members.remove(member.getId());
	}
	
	/**
	 * Returns a member given an id
	 * 
	 * @param memberId Id of the member
	 * 
	 * @return Member Unique instance of that member
	 */
	public Member getMember(String memberId) {
		
		if (memberId.equals(User.ADMIN_CODE)) {
			return User.ADMIN_USER;
		}
		Member member = (Member)members.get(memberId);
		return member;
	}

	/**
	 * Returns an user given an id and a repository to connect
	 * 
	 * @param repositoryId Repository's id
	 * @param userId User's id
	 * 
	 * @return User User
	 */
	public User getUser(String repositoryId,
			 			String userId) {

		User user = (User)members.get(userId);
		if (user == null) {
		
			Repository repository = 
				RepositoryRegistry.getInstance().getRepository(repositoryId);
			Ticket ticket = repository.getTicket();
			ServerProfile profile = repository.getServerProfile();
			SecurityService ss = JLibraryServiceFactory.getInstance(profile).getSecurityService();
			
			try {
				user = ss.findUserById(ticket,userId);
				members.put(user.getId(),user);
			} catch (Exception e) {
				
			   logger.error(e.getMessage(),e);
			}
		}
		return user;
	}	
	
	/**
	 * Singleton 
	 * 
	 * @return Unique instance of this MembersRegistry
	 */
	public static MembersRegistry getInstance() {
		
		if (instance == null) {
			instance = new MembersRegistry();
		}
		return instance;
	}
}
