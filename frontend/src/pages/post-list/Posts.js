import React from "react";
import { Grid, Typography, Paper } from "@mui/material";

const Post = ({ post }) => (
    <Paper elevation={3} style={{ padding: "16px", marginBottom: "16px" }}>
        <Typography variant="h6" gutterBottom>
            Post #{post.id}
        </Typography>
        <Typography>{post.content}</Typography>
    </Paper>
);

const Posts = ({ posts }) => (
    <Grid container xs={12} justifyContent="center" pl={5} pr={5} mt={3}>
        <Grid item container xs={7} spacing={2}>
            <Grid item xs={12}>
                <Typography variant="h4" gutterBottom>
                    Lista Postów
                </Typography>
            </Grid>
            {posts.map((post) => (
                <Grid item xs={12} key={post.id}>
                    <Post post={post} />
                </Grid>
            ))}
        </Grid>
    </Grid>
);

export default Posts;
