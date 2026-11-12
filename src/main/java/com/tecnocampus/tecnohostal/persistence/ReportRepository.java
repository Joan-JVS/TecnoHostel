package com.tecnocampus.tecnohostal.persistence;

import com.tecnocampus.tecnohostal.application.dto.RoomTypeRevenueDTO;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepository {
    private final JdbcClient jdbc;

    public ReportRepository(JdbcClient jdbc){
        this.jdbc = jdbc;
    }

    public List<RoomTypeRevenueDTO> findRevenueByRoom(){
        return jdbc.sql("""
                SELECT types.room_type AS roomType,
                                       COUNT(DISTINCT r.id) AS totalRooms,
                                       COUNT(b.id) AS checkedOutBookings,
                                       COALESCE(SUM(b.total_price), 0) AS totalRevenue,
                                       CASE
                                           WHEN COUNT(b.id) = 0 THEN 0.0
                                           ELSE COALESCE(SUM(b.total_price), 0) / COUNT(b.id)
                                       END AS averageBookingPrice
                                FROM (
                                    SELECT 'SINGLE' AS room_type
                                    UNION ALL
                                    SELECT 'DOUBLE'
                                    UNION ALL
                                    SELECT 'SUITE'
                                ) types
                                LEFT JOIN rooms r ON r.type = types.room_type
                                LEFT JOIN bookings b
                                    ON b.room_id = r.id
                                    AND b.status = 'CHECKED_OUT'
                                GROUP BY types.room_type
                                ORDER BY totalRevenue DESC
                """)
                .query((rs, rowNum) -> new RoomTypeRevenueDTO(
                        rs.getString("roomType"),
                        rs.getInt("totalRooms"),
                        rs.getInt("checkedOutBookings"),
                        rs.getDouble("toalRevenue"),
                        rs.getDouble()
                ))
    }
}
