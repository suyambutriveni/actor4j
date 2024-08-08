/*
 * Copyright (c) 2015-2024, David A. Bauer. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.actor4j.database.jpa;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class JPAOperations {
	public static boolean hasOne(Object primaryKey, Class<?> entityType, EntityManager entityManager) {
		return entityManager.find(entityType, primaryKey)!=null;
	}
	
	public static void insertOne(Object entity, EntityManager entityManager) {
		entityManager.getTransaction().begin();
		entityManager.persist(entity);
		entityManager.getTransaction().commit();
	}
	
	public static void replaceOne(Object entity, EntityManager entityManager) {
		entityManager.getTransaction().begin();
		entityManager.merge(entity);
		entityManager.getTransaction().commit();
	}
	
	public static void updateOne(Object entity, EntityManager entityManager) {
		entityManager.getTransaction().begin();
		entityManager.merge(entity);
		entityManager.getTransaction().commit();
	}
	
	public static <E> void deleteOne(Object primaryKey, Class<E> entityType, EntityManager entityManager) {
		entityManager.getTransaction().begin();
		E reference = entityManager.getReference(entityType, primaryKey);
		entityManager.remove(reference);
		entityManager.getTransaction().commit();
	}
	
	public static <E> E queryOne(String sql, Class<E> entityType, EntityManager entityManager) {
		E result = null;
		
		entityManager.getTransaction().begin();
		TypedQuery<E> query = entityManager.createQuery(sql, entityType);
		result = query.getSingleResult();
		entityManager.getTransaction().commit();
		
		return result;
	}
	
	public static <E> List<E> queryAll(String sql, Class<E> entityType, EntityManager entityManager) {
		List<E> result = null;
		
		entityManager.getTransaction().begin();
		TypedQuery<E> query = entityManager.createQuery(sql, entityType);
		result = query.getResultList();
		entityManager.getTransaction().commit();
		
		return result;
	}
	
	public static <E> E findOne(Object primaryKey, Class<E> entityType, EntityManager entityManager) {
		return entityManager.find(entityType, primaryKey);
	}
	
	public static <E> List<E> findAll(Class<E> entityType, EntityManager entityManager) {
		TypedQuery<E> query = entityManager.createQuery("SELECT * FROM "+entityType.getSimpleName(), entityType);
		return query.getResultList();
	}
}
