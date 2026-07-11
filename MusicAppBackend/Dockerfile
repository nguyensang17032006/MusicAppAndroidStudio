# Sử dụng image Node.js phiên bản gọn nhẹ làm base image
FROM node:22-alpine

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Copy package.json và package-lock.json (nếu có) trước để tận dụng Docker cache
COPY package*.json ./

# Cài đặt các thư viện (chỉ cài package dependencies, bỏ qua devDependencies)
RUN npm install --production

# Copy toàn bộ mã nguồn backend vào container
COPY . .

# Expose port mà ứng dụng Node.js đang chạy
# Bạn có thể đổi lại nếu port trong file .env của bạn khác (ví dụ 3000)
EXPOSE 3000

# Lệnh khởi chạy server khi container bật lên
CMD ["npm", "start"]
