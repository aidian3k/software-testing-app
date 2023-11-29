import { Route, Routes } from 'react-router-dom'
import LoginPage from '../pages/login-page/LoginPage'
import RegisterPage from '../pages/register-page/RegisterPage'
import { Typography } from '@mui/material'
import PostsList from "../pages/post-list/PostsList";

const Home = () => {
    return (
        <Typography>Witaj w naszym serwisie</Typography>
    )
}

const AppRoutes = () => (
    <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route path="/posts" element={<PostsList />} />
  </Routes>
)

export default AppRoutes
