package com.congdinh.cms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.congdinh.cms.entities.News;
import com.congdinh.cms.enums.NewsStatus;

/**
 * Repository for News entity.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    /**
     * Find news by ID with eager fetching of category and author.
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category LEFT JOIN FETCH n.author WHERE n.id = :id")
    Optional<News> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all news with eager fetching, ordered by createdAt DESC.
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category LEFT JOIN FETCH n.author ORDER BY n.createdAt DESC")
    List<News> findAllWithDetails();

    /**
     * Find news by status with eager fetching.
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category LEFT JOIN FETCH n.author WHERE n.status = :status ORDER BY n.createdAt DESC")
    List<News> findByStatusWithDetails(@Param("status") NewsStatus status);

    /**
     * Find news by author ID.
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category LEFT JOIN FETCH n.author WHERE n.author.id = :authorId ORDER BY n.createdAt DESC")
    List<News> findByAuthorIdWithDetails(@Param("authorId") Long authorId);

    /**
     * Find news by category ID.
     */
    @Query("SELECT n FROM News n LEFT JOIN FETCH n.category LEFT JOIN FETCH n.author WHERE n.category.id = :categoryId ORDER BY n.createdAt DESC")
    List<News> findByCategoryIdWithDetails(@Param("categoryId") Long categoryId);

    /**
     * Count news by status.
     */
    long countByStatus(NewsStatus status);

    /**
     * Count total news.
     */
    @Query("SELECT COUNT(n) FROM News n")
    long countTotal();

    /**
     * Count news by category.
     */
    @Query("SELECT n.category.id, n.category.name, COUNT(n) FROM News n GROUP BY n.category.id, n.category.name ORDER BY COUNT(n) DESC")
    List<Object[]> countByCategory();

    /**
     * Check if category has any news articles.
     */
    boolean existsByCategoryId(Long categoryId);
}
