const db = require('../config/db');

function runDailyReset() {
    const tzOffset = 7 * 60; // GMT+7
    const now = new Date();
    const localTime = now.getTime() + (now.getTimezoneOffset() * 60000) + (tzOffset * 60000);
    
    const vnToday = new Date(localTime);
    const yyyy = vnToday.getFullYear();
    const mm = String(vnToday.getMonth() + 1).padStart(2, '0');
    const dd = String(vnToday.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    const vnYesterday = new Date(localTime - 24 * 60 * 60 * 1000);
    const y_yyyy = vnYesterday.getFullYear();
    const y_mm = String(vnYesterday.getMonth() + 1).padStart(2, '0');
    const y_dd = String(vnYesterday.getDate()).padStart(2, '0');
    const yesterdayStr = `${y_yyyy}-${y_mm}-${y_dd}`;

    db.query(`
        UPDATE user_streaks 
        SET current_streak = 0 
        WHERE last_completed_date IS NULL 
           OR (last_completed_date != ? AND last_completed_date != ?)
    `, [todayStr, yesterdayStr])
    .then(() => {
        return db.query(`UPDATE user_streaks SET today_listening_time = 0`);
    })
    .catch(err => {
        console.error('[Cron Job] Error during daily reset:', err.message);
    });
}

function startStreakCron() {
    const tzOffset = 7 * 60; // GMT+7
    
    const getMsToMidnight = () => {
        const now = new Date();
        const localTime = now.getTime() + (now.getTimezoneOffset() * 60000) + (tzOffset * 60000);
        const vnNow = new Date(localTime);
        
        const vnMidnight = new Date(localTime);
        vnMidnight.setHours(24, 0, 0, 0); // Next midnight
        
        return vnMidnight.getTime() - vnNow.getTime();
    };

    const scheduleNext = () => {
        const msToMidnight = getMsToMidnight();
        
        setTimeout(() => {
            try {
                runDailyReset();
            } catch (err) {
                console.error('[Cron Job] Error running daily reset:', err.message);
            }
            scheduleNext();
        }, msToMidnight);
    };

    scheduleNext();
}

module.exports = { startStreakCron };
