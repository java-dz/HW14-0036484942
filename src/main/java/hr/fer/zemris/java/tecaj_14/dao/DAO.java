package hr.fer.zemris.java.tecaj_14.dao;

import java.util.List;

import hr.fer.zemris.java.hw14.VotingUtil.Info;
import hr.fer.zemris.java.hw14.VotingUtil.Poll;

/**
 * Interface that provides data persistence.
 *
 * @author Mario Bobic
 */
public interface DAO {

    /**
     * Returns a {@linkplain Poll} object with the specified <tt>pollID</tt>
     * obtained from the database.
     *
     * @param pollID poll ID
     * @return a Poll with the specified poll ID
     * @throws DAOException if an Exception occurs
     */
    public Poll getPoll(long pollID) throws DAOException;

    /**
     * Returns a <tt>List</tt> of {@linkplain Poll} objects obtained from the
     * database.
     *
     * @return a List of Poll objects
     * @throws DAOException if an Exception occurs
     */
    public List<Poll> getPollList() throws DAOException;

    /**
     * Returns a <tt>List</tt> of {@linkplain Info} objects obtained from the
     * database by selecting poll options for the specified <tt>pollID</tt>.
     *
     * @param pollID poll ID
     * @return a List of Info objects
     * @throws DAOException if an Exception occurs
     */
    public List<Info> getInfoList(long pollID) throws DAOException;

    /**
     * Gives a vote to an object with the specified id by updating the vote
     * value of the specified <tt>id</tt> to plus one in the database.
     *
     * @param id id of the object to be given a vote
     * @throws DAOException if an Exception occurs
     */
    public void vote(long id) throws DAOException;

}
