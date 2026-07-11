const supabase = require('../config/supabase');

const getChatHistory = async (req, res) => {
    const { userId1, userId2 } = req.params;

    if (!userId1 || !userId2) {
        return res.status(400).json({ success: false, message: 'Thiếu userId' });
    }

    try {
        const { data, error } = await supabase
            .from('messages')
            .select('*')
            .or(`and(sender_id.eq.${userId1},receiver_id.eq.${userId2}),and(sender_id.eq.${userId2},receiver_id.eq.${userId1})`)
            .order('created_at', { ascending: true });

        if (error) throw error;

        res.status(200).json({ success: true, data });
    } catch (error) {
        console.error("Lỗi lấy lịch sử chat từ Supabase:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

module.exports = {
    getChatHistory
};
