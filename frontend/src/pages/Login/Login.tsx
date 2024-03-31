import React, { useRef } from "react";
import { Button, Form } from "react-bootstrap";
import { Link } from "react-router-dom";

import styles from "./Login.module.css";

export const Login: React.FC = () => {
  const emailRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);

  function handleClick(event: React.MouseEvent<HTMLElement>) {
    event.preventDefault();
    console.log(emailRef.current?.value);
    console.log(passwordRef.current?.value);
  }

  return (
    <div className={`d-flex align-items-center justify-content-center ${styles.login}`}>
      <Form className={styles.form}>
        <Form.Group controlId="formBasicEmail">
          <Form.Label>Email address</Form.Label>
          <Form.Control type="email" placeholder="Enter email" ref={emailRef} required />
        </Form.Group>

        <Form.Group controlId="formBasicPassword" className="mt-4">
          <Form.Label>Password</Form.Label>
          <Form.Control type="password" placeholder="Password" ref={passwordRef} required />
        </Form.Group>

        <Button
          className="mt-4 w-100"
          variant="primary"
          type="submit"
          onClick={e => handleClick(e)}
        >
          Login
        </Button>

        <div className="text-center mt-3">
          <Link className="text-body-secondary" to="/register">
            Create an account
          </Link>
        </div>
      </Form>
    </div>
  );
};
