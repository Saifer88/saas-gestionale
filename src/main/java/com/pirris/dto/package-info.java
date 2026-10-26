/**
 * Data Transfer Objects that define the HTTP contract of the API.
 *
 * <p>These are intentionally decoupled from the {@code com.pirris.model} JPA
 * entities: the shape here mirrors what the <em>paola_gestionale</em> frontend
 * works with (drafts, aggregate views, reports), not the database tables. The
 * service layer maps between DTOs and entities.</p>
 */
package com.pirris.dto;
