describe("ทดสอบการเชื่อมต่อกับ Backend API", () => {
  const baseUrl = Cypress.env("VITE_API_URL") || "http://localhost:8080";
  const frontendUrl =
    Cypress.env("VITE_FRONTEND_URL") || "http://localhost:5173";
  const apiEndpoint = Cypress.env("VITE_API_SALE_ITEMS") || "/v1/sale-items";

  // เพิ่ม beforeEach เพื่อ log ข้อมูลก่อนเริ่ม test
  beforeEach(() => {
    cy.log("Starting test with:");
    cy.log(`Frontend URL: ${frontendUrl}`);
    cy.log(`Backend URL: ${baseUrl}`);
    cy.log(`API Endpoint: ${apiEndpoint}`);
  });

  // ทดสอบการเรียก API endpoint
  it("ทดสอบการเรียก API endpoint", () => {
    cy.log("Testing API endpoint...");
    // ทดสอบเรียก GET request
    cy.request({
      method: "GET",
      url: `${baseUrl}${apiEndpoint}`,
      failOnStatusCode: false, // ไม่ให้ test fail ถ้า status code ไม่ใช่ 2xx
      log: true, // เพิ่ม logging
    }).then((response) => {
      cy.log("API Response:", response);
      // ตรวจสอบว่าได้รับ response
      expect(response.status).to.exist;
      // ตรวจสอบว่า response มี body
      expect(response.body).to.exist;
      // ตรวจสอบว่า response เป็น array
      expect(response.body).to.be.an("array");
    });
  });

  // ทดสอบการดึงข้อมูลและแสดงผล
  it("ทดสอบการดึงข้อมูลและแสดงผลในหน้าเว็บ", () => {
    cy.log("Testing frontend display...");
    // เข้า route ที่ถูกต้อง
    cy.visit(`${frontendUrl}/sale-items`, {
      timeout: 10000,
      failOnStatusCode: false,
    });

    cy.get("body").should("exist");
    cy.log("Body exists, checking for container...");

    cy.get(".container.mx-auto.px-4", { timeout: 10000 }).should("exist");
    cy.log("Container exists, checking for grid...");

    cy.get(".grid.grid-cols-5.gap-4", { timeout: 10000 }).should("exist");
    cy.log("Grid exists, checking for items...");

    cy.get(".grid.grid-cols-5.gap-4 > div").should("have.length.at.least", 1);
    cy.log("Found at least one item");
  });

  // ทดสอบการจัดการ error
  it("ทดสอบการจัดการ error จาก API", () => {
    cy.log("Testing error handling...");
    // ทดสอบเรียก API ที่ไม่มีอยู่
    cy.request({
      method: "GET",
      url: `${baseUrl}/v1/nonexistent`,
      failOnStatusCode: false,
      log: true, // เพิ่ม logging
    }).then((response) => {
      cy.log("Error Response:", response);
      // ตรวจสอบว่าได้รับ error status code
      expect(response.status).to.be.oneOf([404, 500]);
    });
  });

  // ทดสอบการดึงข้อมูลรายละเอียดของ sale item
  it("ทดสอบการดึงข้อมูลรายละเอียดของ sale item", () => {
    cy.log("Testing item details...");
    // ดึงข้อมูล sale item แรก
    cy.request({
      method: "GET",
      url: `${baseUrl}${apiEndpoint}`,
      failOnStatusCode: false,
      log: true, // เพิ่ม logging
    }).then((response) => {
      cy.log("Items Response:", response);
      if (response.body.length > 0) {
        const firstItemId = response.body[0].id;
        cy.log(`Testing details for item ID: ${firstItemId}`);

        // ทดสอบดึงข้อมูลรายละเอียด
        cy.request({
          method: "GET",
          url: `${baseUrl}${apiEndpoint}/${firstItemId}`,
          failOnStatusCode: false,
          log: true, // เพิ่ม logging
        }).then((detailResponse) => {
          cy.log("Detail Response:", detailResponse);
          expect(detailResponse.status).to.equal(200);
          expect(detailResponse.body).to.have.property("id", firstItemId);
        });
      } else {
        cy.log("No items found in the response");
      }
    });
  });
});
