import React from 'react';
import { Link } from 'react-router-dom';
import { Music, Home, ArrowLeft } from 'lucide-react';
import { Button } from "@/components/ui/button.jsx";

const NotFoundPage = () => {
    return (
        <div className="flex flex-col items-center justify-center min-h-[65vh] px-4 text-center">
            {/* Animated Icon Container */}
            <div className="relative mb-6">
                <div className="absolute inset-0 bg-primary/20 rounded-full blur-2xl animate-pulse"></div>
                <div className="relative flex items-center justify-center w-24 h-24 mx-auto rounded-full bg-primary/10 border-2 border-primary/20 text-primary">
                    <Music size={48} className="animate-bounce" />
                </div>
            </div>

            {/* Error Message */}
            <h1 className="text-6xl font-extrabold tracking-tight text-primary sm:text-7xl">
                404
            </h1>
            <h2 className="mt-4 text-2xl font-bold text-foreground">
                Giai điệu này chưa được viết!
            </h2>
            <p className="mt-2 text-muted-foreground max-w-md mx-auto text-sm sm:text-base">
                Trang hoặc tài nguyên âm nhạc bạn đang tìm kiếm không tồn tại hoặc đã bị di chuyển đi nơi khác.
            </p>

            {/* Action Buttons */}
            <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-4">
                <Link to="/">
                    <Button className="flex items-center gap-2 px-5 py-2 cursor-pointer transition-all duration-200 active:scale-95 shadow-lg shadow-primary/20 hover:shadow-primary/30">
                        <Home size={16} />
                        Về Trang Chủ
                    </Button>
                </Link>
                <Button
                    variant="outline"
                    onClick={() => window.history.back()}
                    className="flex items-center gap-2 px-5 py-2 cursor-pointer transition-all duration-200 active:scale-95"
                >
                    <ArrowLeft size={16} />
                    Quay Lại
                </Button>
            </div>
        </div>
    );
};

export default NotFoundPage;
