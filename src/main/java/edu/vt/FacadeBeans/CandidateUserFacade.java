/*
 * Created by Conner Owens on 2021.11.29
 * Copyright © 2021 Conner Owens. All rights reserved.
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.CandidateUser;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CandidateUserFacade extends AbstractFacade<CandidateUser> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "Jobs4GeeksPU")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public CandidateUserFacade() {
        super(CandidateUser.class);
    }

    /*
     *********************
     *   Other Methods   *
     *********************
     */

    // Returns the object reference of the CandidateUser object whose primary key is id
    public CandidateUser getUser(int id) {
        // The find method is inherited from the parent AbstractFacade class
        return entityManager.find(CandidateUser.class, id);
    }

    // Returns the object reference of the CandidateUser object whose user name is username
    public CandidateUser findByUsername(String username) {
        if (entityManager.createQuery("SELECT c FROM CandidateUser c WHERE c.username = :username")
                .setParameter("username", username)
                .getResultList().isEmpty()) {
            return null;
        } else {
            return (CandidateUser) (entityManager.createQuery("SELECT c FROM CandidateUser c WHERE c.username = :username")
                    .setParameter("username", username)
                    .getSingleResult());
        }
    }

    // Deletes the CandidateUser entity object whose primary key is id
    public void deleteUser(int id) {

        // The find method is inherited from the parent AbstractFacade class
        CandidateUser user = entityManager.find(CandidateUser.class, id);

        // The remove method is inherited from the parent AbstractFacade class
        entityManager.remove(user);
    }

}
