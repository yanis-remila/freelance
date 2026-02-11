import React, { useEffect, useState } from 'react';

function HelloWorld() {
  const [message, setMessage] = useState('');
  const apiUrl = `${process.env.REACT_APP_API_BASE_URL}/hello`;

  console.log(apiUrl)

  useEffect(() => {
    fetch(apiUrl)
      .then((response) => response.text())
      .then((data) => setMessage(data))
      .catch((error) => console.error('Error fetching message:', error));
  }, [apiUrl]);

  return (
    <div>
      <h1>Message from Spring Boot v11:</h1>
      <p>{message}</p>
    </div>
  );
}

export default HelloWorld;
