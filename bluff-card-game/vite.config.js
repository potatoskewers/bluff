import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '/Users/danielyang/Desktop/gs-messaging-stomp-websocket-main/complete/src/main/resources/static',
    emptyOutDir: true,
    sourcemap: true, // Enables source maps for production
  },

})
