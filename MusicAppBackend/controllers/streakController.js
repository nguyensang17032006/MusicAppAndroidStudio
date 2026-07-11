const db = require('../config/db');

// Helper to get dates in Vietnam Time (GMT+7)
function getVietnamDateStrings() {
    const tzOffset = 7 * 60; // GMT+7 in minutes
    const now = new Date();
    const localTime = now.getTime() + (now.getTimezoneOffset() * 60000) + (tzOffset * 60000);
    const vnDate = new Date(localTime);
    
    const yyyy = vnDate.getFullYear();
    const mm = String(vnDate.getMonth() + 1).padStart(2, '0');
    const dd = String(vnDate.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;
    
    // Yesterday
    const vnYesterday = new Date(localTime - 24 * 60 * 60 * 1000);
    const y_yyyy = vnYesterday.getFullYear();
    const y_mm = String(vnYesterday.getMonth() + 1).padStart(2, '0');
    const y_dd = String(vnYesterday.getDate()).padStart(2, '0');
    const yesterdayStr = `${y_yyyy}-${y_mm}-${y_dd}`;
    
    return { todayStr, yesterdayStr };
}

// Format MySQL DATE object to YYYY-MM-DD string timezone-safely
function formatDateString(dateVal) {
    if (!dateVal) return null;
    const d = new Date(dateVal);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
}

const getStreak = async (req, res) => {
    const { userId } = req.params;
    try {
        const { todayStr, yesterdayStr } = getVietnamDateStrings();

        // 1. Get or create streak record
        let [rows] = await db.query('SELECT * FROM user_streaks WHERE user_id = ?', [userId]);
        let streak;

        if (rows.length === 0) {
            // Create record
            await db.query(`
                INSERT INTO user_streaks (user_id, current_streak, max_streak, last_completed_date, today_listening_time)
                VALUES (?, 0, 0, NULL, 0)
            `, [userId]);
            
            streak = {
                user_id: userId,
                current_streak: 0,
                max_streak: 0,
                last_completed_date: null,
                today_listening_time: 0
            };
        } else {
            streak = rows[0];
            
            // Self-healing check (dynamic reset if cron failed or delayed)
            // If the record was last updated on a previous day, reset today's listening time.
            const lastUpdatedDateStr = formatDateString(streak.updated_at);
            if (lastUpdatedDateStr && lastUpdatedDateStr !== todayStr) {
                streak.today_listening_time = 0;
                
                // Also check if streak is broken
                const lastCompletedStr = formatDateString(streak.last_completed_date);
                if (lastCompletedStr !== todayStr && lastCompletedStr !== yesterdayStr) {
                    streak.current_streak = 0;
                }
                
                // Save the self-healed values
                await db.query(`
                    UPDATE user_streaks 
                    SET today_listening_time = ?, current_streak = ?
                    WHERE user_id = ?
                `, [streak.today_listening_time, streak.current_streak, userId]);
            }
        }

        // Format dates before sending back
        streak.last_completed_date = formatDateString(streak.last_completed_date);

        res.status(200).json({ success: true, data: streak });
    } catch (error) {
        console.error("Error in getStreak:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

const trackTime = async (req, res) => {
    const { userId, seconds } = req.body;
    if (!userId || seconds === undefined) {
        return res.status(400).json({ success: false, message: "Missing userId or seconds" });
    }

    try {
        const { todayStr, yesterdayStr } = getVietnamDateStrings();

        // Get current streak status
        let [rows] = await db.query('SELECT * FROM user_streaks WHERE user_id = ?', [userId]);
        let streak;

        if (rows.length === 0) {
            await db.query(`
                INSERT INTO user_streaks (user_id, current_streak, max_streak, last_completed_date, today_listening_time)
                VALUES (?, 0, 0, NULL, 0)
            `, [userId]);
            streak = {
                user_id: userId,
                current_streak: 0,
                max_streak: 0,
                last_completed_date: null,
                today_listening_time: 0
            };
        } else {
            streak = rows[0];
            
            // Self-healing check (dynamic reset if cron failed or delayed)
            const lastUpdatedDateStr = formatDateString(streak.updated_at);
            if (lastUpdatedDateStr && lastUpdatedDateStr !== todayStr) {
                streak.today_listening_time = 0;
                const lastCompletedStr = formatDateString(streak.last_completed_date);
                if (lastCompletedStr !== todayStr && lastCompletedStr !== yesterdayStr) {
                    streak.current_streak = 0;
                }
            }
        }

        // Add seconds
        let newListeningTime = streak.today_listening_time + parseInt(seconds, 10);
        let currentStreak = streak.current_streak;
        let maxStreak = streak.max_streak;
        let lastCompletedDate = streak.last_completed_date;

        const lastCompletedStr = formatDateString(lastCompletedDate);

        // If listening time >= 30 mins (1800s) and not yet completed today
        if (newListeningTime >= 1800 && lastCompletedStr !== todayStr) {
            if (lastCompletedStr === yesterdayStr) {
                currentStreak += 1;
            } else {
                currentStreak = 1; // Streak broken, restart
            }
            lastCompletedDate = todayStr;
            maxStreak = Math.max(maxStreak, currentStreak);

            // Update database with completed streak
            await db.query(`
                UPDATE user_streaks
                SET today_listening_time = ?, current_streak = ?, max_streak = ?, last_completed_date = ?
                WHERE user_id = ?
            `, [newListeningTime, currentStreak, maxStreak, lastCompletedDate, userId]);
        } else {
            // Just update listening time
            await db.query(`
                UPDATE user_streaks
                SET today_listening_time = ?
                WHERE user_id = ?
            `, [newListeningTime, userId]);
        }

        // Prepare updated record to return
        const updatedStreak = {
            user_id: userId,
            current_streak: currentStreak,
            max_streak: maxStreak,
            last_completed_date: lastCompletedDate ? formatDateString(lastCompletedDate) : null,
            today_listening_time: newListeningTime
        };

        res.status(200).json({ success: true, message: "Listening time updated", data: updatedStreak });
    } catch (error) {
        console.error("Error in trackTime:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getStreak,
    trackTime
};
